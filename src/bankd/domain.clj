(ns bankd.domain
  (:use [bankd.core :only [apply-event should exist]]))

(defrecord Client [])

(defn client-created [this event]
  (merge (:data event)
         (assoc this
           :uid (:uid event)
           :account-uids []
           :card-numbers [])))

(defn create-client []
  (apply-event (Client.) client-created nil))

(defn changed-name [this event]
  (assoc this :name (-> event :data :name)))

(defn change-name [client name]
  (should exist client)
  (apply-event client changed-name {:client-uid (:uid client)
                                    :name name})  )
