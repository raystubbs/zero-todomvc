(ns zero.demo.view
  (:require
    [zero.core :refer [act bnd << <<ctx] :as z]
    [zero.extras.db :as-alias db]))

(defn app-view [{:keys [items new-item]}]
  (let [completed-items (vec (filter :completed? items))]
    [:root>
     ::z/css ["node_modules/todomvc-common/base.css"
              "node_modules/todomvc-app-css/index.css"]
     ::z/style {:display "block"}
     ::z/on {:connect (act [::db/patch
                            [{:path [:todo-items]
                              :value []}]])}
     [:section.todoapp
      [:header.header
       [:h1 "todos"]
       [:input.new-todo
        :placeholder "What needs to be done?"
        :autofocus true
        :value new-item
        ::z/on {:input (act [::db/patch [{:path [:new-item] :value (<<ctx ::z/event.data)}]])
                :keydown (act
                           [::z/choose
                            (fn [{:keys [key]}]
                              (case key
                                "Enter"
                                [[::db/patch
                                  [{:path [:todo-items]
                                    :conj {:text new-item
                                           :completed? false
                                           :editing? false}}
                                   {:path [:new-item]
                                    :value ""}]]]
                                
                                nil))
                            (<<ctx ::z/event.data)])}]]
      (when (seq items)
        [:section.main
         [:input#toggle-all.toggle-all
          :type "checkbox"
          ::z/on {:change (act
                            [::db/patch
                             [{:path [:todo-items]
                               :value (mapv #(assoc % :completed? (<<ctx ::z/event.data)) items)}]])}]
         [:label {:for "toggle-all"}
          "Mark all as complete"]
         [:ul.todo-list
          (map-indexed
            (fn [idx {:keys [text editing? completed?]}]
              [:li
               ::z/class (cond-> []
                           editing? (conj "editing")
                           completed? (conj "completed"))
               (if editing?
                 [:input.edit
                  :value text
                  ::z/on {:change (act
                                    [::db/patch
                                     [{:path [:todo-items idx :editing?]
                                       :value false}
                                      {:path [:todo-items idx :text]
                                       :value (<<ctx ::z/event.data)}]])}]
                 [:div.view
                  ::z/on {:dblclick (act
                                      [::db/patch
                                       [{:path [:todo-items idx :editing?]
                                         :value true}]]
                                      [::select-after-render (<<ctx ::z/root) :input.edit])}
                  [:input.toggle
                   :type "checkbox"
                   :checked completed?
                   ::z/on {:change (act [::db/patch
                                         [{:path [:todo-items idx :completed?]
                                           :value (not completed?)}]])}]
                  [:label text]
                  [:button.destroy
                   ::z/on {:click (act [::db/patch
                                        [{:path [:todo-items]
                                          :clear #{idx}}]])}]])])
            items)]])
      (when (seq items)
        [:footer.footer
         [:span.todo-count
          [:strong (count items)]
          (if (= 1 (count items)) " item " " items ")
          "left"]
         (when (seq completed-items)
           [:button.clear-completed
            ::z/on {:click (act [::db/patch
                                 [{:path [:todo-items]
                                   :value (vec (remove :completed? items))}]])}
            "Clear completed"])])]
     [:footer.info
      [:p "Double-click to edit a todo"]
      [:p
       "Created by "
       [:a {:href "http://github.com/raystubbs"}
        "Ray"]]
      [:p
       "Part of "
       [:a {:href "http://todomvc.com"}
        "TodoMVC"]]]]))
