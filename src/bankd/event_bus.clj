(ns bankd.event-bus
  (require [lamina.core :as lamina]))

(defonce *event-bus* (atom nil))
(defonce *subscriptions* (atom #{}))

(defprotocol EventBus
  (publish [this event])
  (subscribe [this event-name handler]))

(extend-type lamina.core.channel.Channel
  EventBus
  (publish [ch event]
    (lamina/enqueue ch event))
  (subscribe [ch event-name handler]
    (lamina/receive-all (lamina/filter* #(= (:name %) (-> event-name name symbol))
                                        ch)
                        handler)))

(defn in-process-bus []
  (swap! *event-bus* lamina/channel)
  (doseq [[event-name subscriber] @*subscriptions*]
    (subscribe @*event-bus* event-name subscriber)))


(defn add-subscriber [event-name subscriber]
  (swap! *subscriptions* conj [event-name subscriber]))

(defn publish-event [event]
  (publish @*event-bus* event))

