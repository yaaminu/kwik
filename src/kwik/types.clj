(ns kwik.types)


(defstruct kwik-value
  ; The base type for storing data into
  ; kwik. To store a value in the db, you specify it's type
  ; and it's corresponding value. example to map "foo" to ["foo" "barz"]
  ; you'd wrap it in {:type "list" :value ["foo" "barz"]}
  :type :value)
