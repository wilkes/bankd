(ns bankd.commands
  (:use [bankd.core :only [fetch-by-id]])
  (:require [bankd.domain :as dom])
  (:import [bankd.domain Client]))

(defn create-client []
  (dom/create-client))

(defn name-client [id name]
  (dom/change-name (fetch-by-id Client id) name))

