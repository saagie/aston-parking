module Views.Topbar exposing (viewTopbar)

import Html exposing (..)
import Html.Attributes exposing (..)
import Models.Spot exposing (Spot)
import Models.User exposing (User)
import Views.Topbar.User exposing (viewUserImage, viewUserName)
import Views.Topbar.Spot exposing (viewSpot)


viewTopbar : User -> Spot -> Html msg
viewTopbar user spot =
    div [ class "l-navbar as--success" ]
        [ viewUserImage user
        , viewUserName user
        , viewSpot spot
        ]
