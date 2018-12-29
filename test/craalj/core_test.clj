(ns craalj.core-test
  (:require [clojure.test :refer :all]
            [craalj.core :refer :all]))

(def peval (build-context [:r]))

(deftest r-constant-test
  (testing "R constant"
    (is (= (peval :r "39L") 39))))

(deftest r-func-test
  (testing "R function"
    (is (= ((peval :r "function(x)x-1L") 40) 39))))

(deftest r-list-test
  (testing "R lists"
    (let [ret (peval :r "list(1:3,7:9)")]
      (is (= (count ret) 2))
      (is (= (first ret) [1 2 3]))
      (is (= (second ret) [7 8 9])))))
