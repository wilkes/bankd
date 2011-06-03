(ns bankd.event-storage)

(def *event-log* (atom {}))

(defn- fetch-aggregate [event-log id]
  (get event-log id {:uid id
                     :version 0
                     :snapshot {}
                     :events []}))

(defn commit [event-log event]
  (let [id (:aggregate-uid event)
        aggregate (fetch-aggregate event-log id)
        updated-aggregate (assoc aggregate
                            :version (:version event)
                            :events (conj (:events aggregate) event))]
    (assoc event-log id updated-aggregate)))

(defn event-versions [events]
  (let [versions (reduce (fn [result event]
                           (conj result [(:aggregate-uid event)
                                         (:originating-version event)]))
                         [] events)]
    (set versions)))

(defn check-versions [event-log events]
  (doseq [[id ver] (event-versions events)]
    (let [aggregate (fetch-aggregate event-log id)]
      (when-not (= ver (:version aggregate))
        (throw (RuntimeException. "Concurrency Exception"))))))

(defn save-events* [event-log events]
  (check-versions event-log events)
  (reduce commit event-log events))

(defn save-events [events]
  (swap! *event-log* save-events* events))

(defn fetch-by-id [type-var id]
  (reduce (fn [obj event]
            (let [f (ns-resolve (:ns event) (:name event))]
              (assoc (f obj event) :version (:version event))))
          ^{:type type-var} {}
          (get-in @*event-log* [id :events])))
