(ns zero.demo.app
  (:require
   [zero.core :refer [<< act bnd] :as z]
   [zero.config :as zc]
   [zero.extras.util :as zu]
   [zero.extras.dom :as zd]
   [zero.extras.db :as db]
   [zero.demo.view :as view]))

(zc/reg-injections
  ::view/css-urls
  (fn []
    [(js/URL. "node_modules/todomvc-common/base.css" js/document.baseURI)
     (js/URL. "node_modules/todomvc-app-css/index.css" js/document.baseURI)])

  ::view/match-key?
  (fn [{data ::z/event.data} {:keys [key mods code]}]
    (and
      (or (nil? key) (= key (:key data)))
      (or (nil? code) (= code (:code data)))
      (= (set (:mods data)) (set mods)))))

(zc/reg-effects
  ::view/select-after-render
  (fn [^js/ShadowRoot root selector]
    (zd/listen root "render" ::focus-after-render
      (fn []
        (when-let [target ^js/Node (.querySelector root (zu/css-selector selector))]
          (.select target)))
      :once? true)))

(zc/reg-components
  :z/app
  {:props {:items (bnd ::db/path [:todo-items])
           :new-item (bnd ::db/path [:new-item])}
   :view view/app-view})