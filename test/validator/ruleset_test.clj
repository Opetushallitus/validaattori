(ns validator.ruleset-test
  (:use midje.sweet
        validator.ruleset)
  (:require [validator.core :refer [Validatable supressed]]))


(extend-type clojure.lang.Keyword
  Validatable
  (validate [this] :unifinished)
  (supressed [this] :unifinished))


(extend-type midje.data.metaconstant.Metaconstant
  Rule
  (applies? [rule resource] :unifinished)
  (problems? [rule resource] :unifinished)
  (id [rule] "id of the rule" :unifinished))

(facts "about ruleset"
       (fact "it returns a single applicable rule for resource"
             (find-rules
              (ruleset
               ...rule...) :resource) => (contains [...rule...])
             (provided
              (applies? ...rule... :resource) => true))

       (fact "it returns multiple applicable rules"
             (find-rules (ruleset
                          ...rule1...
                          ...rule2...) :resource) => (contains [...rule1... ...rule2...])
             (provided
              (applies? ...rule1... :resource) => true
              (applies? ...rule2... :resource) => true))


       (fact "it returns only applicable rules"
             (find-rules (ruleset
                          ...rule1...
                          ...rule2...) :resource) => (just [...rule1...])
             (provided
              (applies? ...rule1... :resource) => true
              (applies? ...rule2... :resource) => false))


       (fact "it does not return supressed rules"
             (find-rules (ruleset
                          ...rule1...
                          ...rule2...) :resource) => (just [...rule1...])
             (provided
              (id ...rule1...) => :rule1
              (id ...rule2...) => :rule2
              (supressed :resource) => #{:rule2})))


(facts "about rule"
       (fact "it returns a message problem resource"
             (problems?
              (rule "example" :validator (constantly false))
              ...resource...) => (contains ["...resource... failed rule example"]))
       (fact "it returns an empty vector for resource with no problems"
             (problems?
              (rule "example" :validator (constantly true))
              ...resource...) => []))










