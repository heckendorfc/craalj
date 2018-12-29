(ns craalj.core
  (:import (org.graalvm.polyglot Context Value)))

;http://www.graalvm.org/sdk/javadoc/org/graalvm/polyglot/Value.html
(defn ->clojure [polyglot-value]
  (defn getarr [polyval]
    (for [i (range (.getArraySize polyval))]
      (-> (.getArrayElement polyval i)
          ->clojure)))
  (cond
    (and (.hasArrayElements polyglot-value)
         (> (.getArraySize polyglot-value) 1)) (getarr polyglot-value)
    (.isString polyglot-value) (.toString polyglot-value)
    (.isNull polyglot-value) nil
    (.isBoolean polyglot-value) (.asBoolean polyglot-value)
    (.isProxyObject polyglot-value) 'proxy ;TODO
    (.fitsInLong polyglot-value) (.asLong polyglot-value)
    (.fitsInDouble polyglot-value) (.asDouble polyglot-value)
    (.canExecute polyglot-value) (fn [& args]
                                   (-> (.execute polyglot-value (into-array args))
                                       ->clojure))
    :else (-> polyglot-value
              (.toString)
              (clojure.edn/read-string))))

(defn build-context [langs]
 (let [langmap {:r "R" :js "js" :llvm "llvm" :ruby "ruby" :python "python"}
       context (-> (Context/newBuilder (into-array ((apply juxt langs) langmap)))
                   (.allowAllAccess true)
                   (.build))]
   (fn [lang code]
     (->clojure (.eval context (lang langmap) code)))))

(comment ; can also bind vars in this way
  (.eval context "R" "sumwrap=function(x)sum(x)")
  (.getMember rbind "sumwrap")
  (def rsumwrap (.getMember rbind "sumwrap"))
  (.execute rsumwrap (into-array [[1 2]])))
