(ns bankd.command-bus
  (:use [bankd.core :only [uuid]]
   [bankd.event-bus :only [publish-event]]
        [bankd.event-storage :only [commit]]))
(declare *domain-repository*)

(defn domain-repository-events []
  @*domain-repository*)

(defn add-event-to-domain-repository [event]
  (swap! *domain-repository* conj event))

(defmacro with-domain-repository [& body]
  `(binding [*domain-repository* (atom [])]
     (let [result# ~@body]
       (doseq [event# (domain-repository-events)]
         (commit event#)
         (publish-event event#))
       result#)))

(defn execute-command [cmd & args]
  (with-domain-repository
    (apply cmd args)))

(defn apply-event [aggregate event-handler attributes]
  (let [event (uuid {:name (:name (meta event-handler))
                     :ns (:ns (meta event-handler))
                    :data attributes})
        aggregate (event-handler aggregate event)]
    (add-event-to-domain-repository (assoc event :aggregate-uid (:uid aggregate)))
    aggregate))
