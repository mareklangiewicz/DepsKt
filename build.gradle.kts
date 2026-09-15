// The root project is an empty aggregator on purpose: :deps and :templatefun are siblings.
// Nothing is published from here. See docs/design/lib-details-denesting.md.
//
// Note: this root cannot use the templatefun conventions its own :templatefun subproject provides
// (that would be circular), and it deliberately does NOT apply the deps plugin either -- it has
// no sources. The settings plugin (published, pinned in settings.gradle.kts) is all it needs.

import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.utils.*

defaultGroupAndVerAndDescription(gradle.extLib)
