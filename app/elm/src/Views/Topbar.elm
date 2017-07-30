module Views.Topbar exposing (viewTopbar)

import Html exposing (..)
import Html.Attributes exposing (..)
import Models.User exposing (User)

viewTopbar: User -> Html msg
viewTopbar user =
  div [ class "l-navbar as--success" ]
      [ div [ class "l-navbar__avatar" ]
            [ div [ class "o-avatar" ]
                  [ img [ src user.image ] [] ] ]
      , div [ class "l-navbar__username" ]
            [ text user.name ]
      , div [ class "l-navbar__spot" ]
            [ div [ class "l-navbar__spot-label" ]
                  [ strong [] [ text "Today" ]
                  , text "Spot" ]
            , div [ class "l-navbar__spot-value" ]
                  [ text "103" ] ]
      ]
