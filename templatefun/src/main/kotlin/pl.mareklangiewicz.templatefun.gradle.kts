// The precompiled script plugin that gives a consuming build script access to templatefun.
//
// Applying it is what puts templatefun (and its Lib model) on that script's compile classpath, so
// `import pl.mareklangiewicz.templatefun.*` resolves and `defaultBuildTemplateForBasicMppLib(..)`
// can be called. That is its whole job -- it configures nothing by itself, deliberately: the
// templates are ordinary functions a script calls when it wants them, not conventions applied
// behind its back.
//
// It replaces KGround's local `my-convention`, which did the same thing from an includeBuild.
//
// The one thing it does prove, by existing: a .gradle.kts compiled WITHOUT -Xcontext-parameters
// can call into sources compiled WITH it. See KGround's probes.
import pl.mareklangiewicz.templatefun.*

context(project) { testContextParameters() }
