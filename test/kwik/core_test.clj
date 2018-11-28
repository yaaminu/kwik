(ns kwik.core-test
  (:require [clojure.test :refer :all]
            [clj-http.client :as http]))



(def options {:throw-exceptions false :as :clojure})
(def host "http://localhost:8000")

(deftest general-tests
  (testing "commands not found"
    (let [{status :status reply :body} (http/get (str host "/unknown-commands/key?args") options)
          error-code (get reply "code")]
      (is (= 400 status) "Status code for failed requests should be 400")
      (is (= 103 error-code) "Must reply with a 103 error code when a commands does not exist")
      ))

  (testing "Inavlid url"
    (let [error-message "should return a 404 status code for invalid requests"
          {status-1 :status} (http/get (str host "/key") options)
          {status-3 :status} (http/get (str host "/key?args=test") options)
          {status-2 :status} (http/get host options)]
      (is (= 404 status-1) error-message)
      (is (= 404 status-2) error-message)
      (is (= 404 status-3) error-message)
      ))
  )