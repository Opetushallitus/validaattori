(ns validator.core)

(defprotocol Validatable
  "Validator for some resource"
  (validate [resource] "validate the resource")
  (suppressed [resource] "suppressed rules"))
