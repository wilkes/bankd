(ns bankd.support.command-bus
  (:use [bankd.support.event-storage :only [save-events]]
        [bankd.support.event-bus :only [publish-event]]))

(declare *domain-repository*)

(defn uuid [instance]
  (merge {:id (str (java.util.UUID/randomUUID))} instance))

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

(defn- make-event [event-handler originating-version attributes]
  (uuid {:name (:name (meta event-handler))
                         :ns (:ns (meta event-handler))
                         :originating-version originating-version
                         :data attributes}))

(defn apply-event!
  ([aggregate event-handler attributes]
     (apply-event! (assoc aggregate :version 0) 0 event-handler attributes))

  ([aggregate originating-version event-handler attributes]
      (let [event (make-event event-handler originating-version attributes)
            aggregate (assoc (event-handler aggregate event)
                        :version (-> aggregate :version inc))]
        (add-event-to-domain-repository!
         (assoc event
           :aggregate-id (:id aggregate)
           :version (:version aggregate)))
        aggregate)))
