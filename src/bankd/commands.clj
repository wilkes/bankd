(ns bankd.commands
  (:use [bankd.event-storage :only [fetch-by-id]])
  (:require [bankd.domain :as dom])
  (:import [bankd.domain Client]))

(defn create-client [params]
  (dom/create-client params))

(defn name-client [id version name]
  (dom/change-name (fetch-by-id Client id)
                   version
                   name))

