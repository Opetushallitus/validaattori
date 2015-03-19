# validaattori

Datavalidaatio sekä selaimelle että serverille samalla koodilla.

## testien ajaminen

    lein midje

## hakurekisteri-testisivu

Buildaa hakurekisterin validaattori (prod build)

    lein with-profile hakurekisteri cljsbuild once prod
    open hakurekisteri/testpage/prod.html

Tai buildaa dev-build

    lein with-profile hakurekisteri cljsbuild once dev
    open hakurekisteri/testpage/index.html

Avaa tiedostovalitsimella testitiedosto `esimerkki-arvosanat.xml`.
