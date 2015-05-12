# validaattori

[![Join the chat at https://gitter.im/Opetushallitus/validaattori](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Opetushallitus/validaattori?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Datavalidaatio sekä selaimelle että serverille samalla koodilla.

## testien ajaminen

    lein midje
    hakurekisterin testit mukaan:
    lein hr midje

## hakurekisteri-testisivu

Buildaa hakurekisterin validaattori (prod build)

    lein hr-prod
    open hakurekisteri/testpage/prod.html

Tai buildaa dev-build

    lein hr-dev
    open hakurekisteri/testpage/index.html

Avaa tiedostovalitsimella testitiedosto `esimerkki-arvosanat.xml`.
