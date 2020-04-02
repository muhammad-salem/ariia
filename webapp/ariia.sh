# ----------------- creation --------------------- #
## ng new ariia
#ng new ariia -v --createApplication=false --directory=webapp --interactive=false
#cd webapp/

#ng generate application ariia --style=scss --routing=true --prefix=ariia

#ng generate library shared-libaray

# ----------------- creation --------------------- #

# ---------------- components -------------------- #
# website
ng g c component/website/home-page
ng g c component/website/about-page

# navbar component
ng g c component/structure/layout
ng g c component/structure/header
ng g c component/structure/footer
ng g c component/structure/site-navbar

# user
## ng g c component/user/register
ng g c component/user/login
ng g c component/user/logout
ng g c component/user/user-profile
ng g c component/user/header-user-profile

# downloads
ng g c component/downloads/item
ng g c component/downloads/download-list
ng g c component/downloads/compact-item-view
ng g c component/downloads/history
ng g c component/downloads/add-link

# server
ng g c component/server/site-setting
ng g c component/server/server-setting

# notify
ng g c component/notify/notification

# ---------------- components -------------------- #

# ------------------ services -------------------- #

ng g service service/site-info
ng g service service/user-info

# user
ng g service service/register
ng g service service/login
ng g service service/logout

# item
ng g service service/item
ng g service service/download-action

# server
ng g service service/site-setting
ng g service service/server-setting


# ------------------ services -------------------- #

# ------------------- model ---------------------- #

ng g class model/site
ng g class model/user
ng g class model/item
ng g class model/site-setting
ng g class model/server-seting
ng g class model/history

# ------------------- modal ---------------------- #

# ------------------- package -------------------- #

## npm i jquery popper.js --save
## ng add ngx-bootstrap
## npm i @ngx-translate/core --save
## npm i @ngx-translate/http-loader --save
## npm i
## npm audit fix

# ------------------- package -------------------- #

