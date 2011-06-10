(ns bankd.support.core)

(defn exist [o]
  (not (nil? (:id o))))

(defn should [f? o]
  (when-not (f? o)
    (let [t (pr-str (type o))
          v (:name (meta f?))]
      (throw (RuntimeException. (str t " should " v "."))))))
