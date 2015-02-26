(ns validator.core)

(defprotocol Validatable
  "Validator for some resource"
  (validate [resource] "validate the resource")
  (supressed [resource] "supressed rules"))





