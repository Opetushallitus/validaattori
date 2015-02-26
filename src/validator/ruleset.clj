(ns validator.ruleset
  (:require [validator.core :refer [supressed]]))

(defprotocol Ruleset
  "ruleset for validation"
  (find-rules [rules resource] "find applicable rules for a resource"))

(defprotocol Rule
  "a single rule for validation"
  (applies? [rule resource] "if this rule applies to given resource")
  (problems? [rule resource] "find problems if resource")
  (id [rule] "id of the rule")
  (message [rule resource] "compose a message for problematic resource"))

(extend-type clojure.lang.IFn
  Ruleset
  (find-rules [this resource] (this resource)))

(extend-type clojure.lang.IPersistentMap
  Rule
  (applies? [this resource] ((this :applies) resource))
  (problems? [this resource] (if
                               ((this :validator) resource)
                               []
                               [(message this resource)]))
  (id [this] (:id this))
  (message [this resource] (str resource " failed rule " (id this))))

(defn ruleset [& rules]
  (fn [r]
      (filter
              #(and
                (applies? %1 r)
                (not ((supressed r) (id %1))))
              rules)))

(defn rule [name & {:keys [applies validator] :or {applies (constantly true)}}]
  {:id name :applies applies :validator validator})
