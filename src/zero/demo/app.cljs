(ns zero.demo.app
  (:require
   [zero.core :refer [<< act bnd] :as z]
   [zero.config :as zc]
   [zero.extras.db :as db]
   [zero.dom :as zd]
   [zero.demo.view :as view]
   [zero.component]))

(zc/reg-effects
  ::view/select-after-render
  (fn [^js/ShadowRoot root selector]
    (zd/listen ::focus-after-render root "render"
      (fn []
        (when-let [target ^js/Node (.querySelector root (z/css-selector selector))]
          (.select target)))
      :once? true)))

(zc/reg-components
  :z/app
  {:props {:items (bnd ::db/path [:todo-items])
           :new-item (bnd ::db/path [:new-item])}
   :view view/app-view})