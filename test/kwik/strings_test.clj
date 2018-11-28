(ns kwik.strings-test
  (:require [clojure.test :refer :all]
            [clj-http.client :as http]))

(def options {:throw-exceptions false :as :clojure})
(def host "http://localhost:8000")


(deftest SET

  (testing "successful set command"
    (let [{status :status body :body} (http/get (str host "/set/test-set-key?args=test-set-value") options)
          {value :body} (http/get (str host "/get/test-set-key") options)]
      (is (= status 200) "Server must respond with a 200 status code for successful requests")
      (is (= (str body) "OK") "Server must reply with OK if a command succeeds")
      (is (= (str value) "test-set-value") "Must correctly return the value mapped to a given key")))


  (testing "Failed set command"
    (testing "arity"
      (let [{status :status body :body} (http/get (str host "/set/key?args") options)]
        (is (= status 400))                                 ; set expects 2 arguments (key inclusive)
        (is (= 102 (get body "code")), "Must return error code for wrong arity when incorrect number of arguments is passed")
        ))
    )
  )



(deftest GET
  (testing "successful get command"
    (let [{set-reply :body set-status-code :status} (http/get (str host "/set/test-get-key?args=test-get-value") options)

          {get-status-code :status value :body} (http/get (str host "/get/test-get-key") options)]

      (is (= (str set-reply) "OK") "Server must reply with OK if a command succeeds")
      (is (= set-status-code 200) "Server must respond with a 200 status code for successful requests")

      (is (= get-status-code 200) "Server must respond with a 200 status code for successful requests")
      (is (= (str value) "test-get-value"))))



  (testing "failed get command"
    (testing "unmapped key"
      (let [{reply :body status :status} (http/get (str host "/get/unmapped-key") options)
            error-code (get reply "code")]

        (is (= status 400) "Status code for failed commands should be 400")
        (is (= error-code 100) "Server should return a 100 error code when mapping does not exist")
        ))

    (testing "get against wrong value type"
      (let [{lset-status-code :status} (http/get (str host "/lset/test-get-incorrect-type-key?args=value1,value2") options)

            {get-incorrect-type-reply :body get-incorrect-status-code :status} (http/get (str host "/get/test-get-incorrect-type-key") options)
            error-code (get get-incorrect-type-reply "code")]

        (is (= lset-status-code 200) "Server must respond with a 200 status code for successful requests")

        (is (= get-incorrect-status-code 400) "Status code for failed commands should be 400")
        (is (= error-code 101) "Server should return a 101 error code when a command is executed against incorrect data type")
        ))
    ))