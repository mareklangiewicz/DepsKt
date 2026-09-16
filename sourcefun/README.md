# SourceFun

> Moved: SourceFun used to be its own repo (`mareklangiewicz/SourceFun`). It now lives here, in
> DepsKt, as the `:sourcefun` sibling of `:deps` and `:templatefun`. The plugin id
> (`pl.mareklangiewicz.sourcefun`) and the published coordinates
> (`pl.mareklangiewicz.deps:SourceFun`) are unchanged; only the version numbering jumped, from
> 0.4.50 to DepsKt's shared version. See `../docs/design/lib-details-denesting.md`.

This subproject allows to easily maintain typical Kotlin/Java/Android projects by automating some source code changes.
It's a gradle "convention plugin": https://docs.gradle.org/current/samples/sample_convention_plugins.html
