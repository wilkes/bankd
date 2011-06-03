(ns bankd.test.services
  (:use bankd.services
        midje.sweet))

;.;. When someone asks you if you're a god, you say 'YES'! -- Zeddemore
(fact "name-client increments the version of the client"
  (background
   (around :facts (let [id (:uid (create-client {:name .old-name.}))] ?form)))
  (name-client {:id id :version 1 :name .new-name.}) => (contains {:uid id
                                                                   :version 2
                                                                   :name .new-name.}))
