(ns bankd.domain
  (:use [bankd.core :only [should exist]]
        [bankd.command-bus :only [apply-event!]]))

(defrecord Client [])

(defn client-created [this event]
  (merge (:data event)
         (assoc this
           :id (:id event)
           :account-ids []
           :card-numbers [])))

(defn create-client [params]
  (apply-event! (Client.) client-created params))

(defn changed-name [this event]
  (assoc this :name (-> event :data :name)))

(defn change-name [client version name]
  (should exist client)
  (apply-event! client version changed-name
                {:client-id (:id client)
                 :name name}))
