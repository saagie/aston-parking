module Views.Topbar.Spot exposing (viewSpot)

import Html exposing (..)
import Html.Attributes exposing (..)
import Models.Spot exposing (Spot)


viewSpot : Spot -> Html msg
viewSpot spot =
    div [ class "l-navbar__spot" ]
        [ div [ class "l-navbar__spot-label" ]
            [ strong [] [ text "Today" ]
            , text "Spot"
            ]
        , div [ class "l-navbar__spot-value" ]
            [ text (toString spot.number) ]
        ]
