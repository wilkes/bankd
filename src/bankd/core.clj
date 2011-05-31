(ns bankd.core)

(defn uuid [instance]
  (merge {:uid (str (java.util.UUID/randomUUID))} instance))

(defn exist [o]
  (not (nil? (:uid o))))

(defn should [f? o]
  (when-not (f? o)
    (let [t (pr-str (type o))
          v (:name (meta f?))]
      (throw (RuntimeException. (str t " should " v "."))))))
