(ns hakurekisteri.perusopetus-test
  (:use midje.sweet
        hakurekisteri.perusopetus
        validator.rules)
  (:require [validator.core :refer [Validatable validate]]))



(defn fails-rule [resource rule]
  (contains [(just {:resource resource :rule (contains {:id rule})})]))


(defmulti create-arvosana type)
(defmethod create-arvosana clojure.lang.PersistentHashSet [s]
  (rand-nth (seq s)))
(defmethod create-arvosana :default [s]
  s)

(defn todistus [s] (->Todistus
                    (->Suoritus "1.2.246.562.13.62959769647" "VALMIS" (->Oppija "010101-123N" "Blomqvist" "Eetu"))
                    (map #(->Arvosana (create-arvosana %1) 8 "" false) s)
                    #{}))

(def empty-todistus (todistus []))

(facts "about mandatory subjects in perusopetuksen todistus"
       (fact "Äidinkieli is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-ai))
       (fact "A1 kieli is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-a1))
       (fact "Matematiikka is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-ma))
       (fact "Käsityö is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-ks))
       (fact "Terveystieto is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-te))
       (fact "Kotitalous is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-ko))
       (fact "Biologia is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-bi))
       (fact "Musiikki is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-mu))
       (fact "Liikunta is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-li))
       (fact "Katsomustieto is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-kt))
       (fact "Maantieto is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-ge))
       (fact "Kuvaamataito is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-ku))
       (fact "Kemia is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-ke))
       (fact "Fysiikka is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-fy))
       (fact "B1 or A2 kieli is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-a2-or-b1))
       (fact "Käsityö is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-ks))
       (fact "Yhteiskuntaoppi is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-yh))
       (fact "Historia is mandatory"
             (validate empty-todistus) => (fails-rule empty-todistus :mandatory-hi)))

(fact "valid todistus has no problems"
      (validate (todistus mandatory-subjects)) => [])
