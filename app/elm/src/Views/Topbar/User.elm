module Views.Topbar.User exposing (viewUserImage, viewUserName)

import Html exposing (..)
import Html.Attributes exposing (..)
import Models.User exposing (User)


viewUserImage : User -> Html msg
viewUserImage user =
    div [ class "l-navbar__avatar" ]
        [ div [ class "o-avatar" ]
            [ img [ src user.image ] [] ]
        ]


viewUserName : User -> Html msg
viewUserName user =
    div [ class "l-navbar__username" ]
        [ text user.name ]
