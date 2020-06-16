## ---------------- creation --------------------- #
ng new ariia -v --createApplication=false --directory=webUI
cd webUI/
ng g application ariia --style=scss --routing=true
# ----------------- creation --------------------- #

## --------------- components -------------------- #
ng g c doc-root

#core-api
ng g library core-api
ng g class model/data --project=core-api
ng g class model/range-info --project=core-api
ng g class model/item --project=core-api
ng g class model/metalink-item --project=core-api
ng g class model/message --project=core-api
ng g class model/network-session --project=core-api
ng g class model/session-history --project=core-api
ng g class model/properties --project=core-api
ng g class model/filter --project=core-api
ng g class model/ui --project=core-api

ng g service service/data --project=core-api
ng g service service/item --project=core-api
ng g service service/log --project=core-api
ng g service service/range --project=core-api
ng g service service/range.info --project=core-api
ng g service service/server.setting --project=core-api
ng g service service/site.setting --project=core-api
ng g service service/sse --project=core-api

ng g pipe pipe/filter-by --project=core-api
ng g pipe pipe/unit-length --project=core-api
ng g pipe pipe/search --project=core-api
ng g pipe pipe/up-time --project=core-api

#downloads
ng g module modules/downloads
ng g c modules/downloads/item-view
ng g c modules/downloads/item-list
ng g c modules/downloads/item-table
ng g c modules/downloads/download-view

#log-message
ng g module modules/logs
ng g c modules/logs/log-table
ng g c modules/logs/log-view

#links
ng g module modules/links
ng g c modules/links/link
ng g c modules/links/meta-link
ng g c modules/links/add-link-viewer

#network
ng g module modules/network
ng g c modules/network/network-chart
ng g c modules/network/session-monitor
ng g c modules/network/network-viewer


#setting
ng g module modules/setting
ng g c modules/setting/site-settings
ng g c modules/setting/server-settings
ng g c modules/setting/site-settings-viewer

#material
ng g module modules/material

#shared theme-picker
ng g module modules/shared/theme-picker
ng g class modules/shared/theme-picker/theme-storage
ng g service modules/shared/theme-picker/style-manager


#shared layout
ng g m modules/shared/layout
ng g c modules/shared/layout/header
ng g c modules/shared/layout/footer
ng g c modules/shared/layout/sidebar

#shared default
ng g m modules/shared/default
ng g c modules/shared/default/default

#shared dashboard
ng g m modules/shared/dashboard
ng g c modules/shared/dashboard/dashboard


# ---------------- components -------------------- #

## ------------------ package -------------------- #

ng add @angular/material

# ------------------- package -------------------- #


