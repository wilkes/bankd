(ns bankd.command-bus
  (:use [bankd.core :only [uuid]]
        [bankd.event-storage :only [save-events]]
        [bankd.event-bus :only [publish-event]]))
(declare *domain-repository*)

(defn domain-repository-events []
  @*domain-repository*)

(defn add-event-to-domain-repository! [event]
  (swap! *domain-repository* conj event))

(defn commit-repository! []
  (let [events (domain-repository-events)]
    (save-events events)
    (dorun (map publish-event events))))

(defmacro with-domain-repository [& body]
  `(binding [*domain-repository* (atom [])]
     (let [result# ~@body]
       (commit-repository!)
       result#)))

(defn execute-command [cmd & args]
  (with-domain-repository
    (apply cmd args)))

(defn apply-event!
  ([aggregate event-handler attributes]
     (apply-event! (assoc aggregate :version 0) 0 event-handler attributes))
  ([aggregate originating-version event-handler attributes]
      (let [event (uuid {:name (:name (meta event-handler))
                         :ns (:ns (meta event-handler))
                         :data attributes})
            aggregate (assoc (event-handler aggregate event)
                        :version (-> aggregate :version inc))]
        (add-event-to-domain-repository!
         (assoc event
           :aggregate-uid (:uid aggregate)
           :originating-version originating-version
           :version (:version aggregate)))
        aggregate)))
