module Main exposing (..)

import Html exposing (Html)

import Models.User exposing (User)
import Views.Topbar exposing (viewTopbar)

type alias Model =
  { user: User
  }

model: Model
model =
  { user =
    { name = "Ben Morris"
    , image = "https://source.unsplash.com/random/100x100"
    }
  }

type Message = UserLoggedIn

update: Message -> Model -> ( Model, Cmd Message )
update message model = ( model, Cmd.none )

view: Model -> Html Message
view model = viewTopbar model.user

init : ( Model, Cmd Message )
init =
    ( model, Cmd.none )

main : Program Never Model Message
main =
    Html.program
        { view = view
        , init = init
        , update = update
        , subscriptions = always Sub.none
        }
