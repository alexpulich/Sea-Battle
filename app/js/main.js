//Fullscreen scrolling midule
var scroll = (function() {
    var _fullpageElem = $('#fullpage'),

        _addScroll = function() {
            _fullpageElem.fullpage({
                anchors: ['main', 'about'],
                menu: '#nav'
            });
        };

    return {
        init: _addScroll
    }
})();



// var dragAndDrop = (function() {

//     //Setup module
//     function _setup() {
//         _setDragAndDrop();
//         $('.ship').on('click', _rotateShip);
//         $('.game-field-inner').on('drop', _dropShip);
//         $('.game-field-inner').on('dropout', _dropOutShip);
//     }

//     //Setting jQuery-UI droppable and draggable to
//     //field and ships
//     function _setDragAndDrop() {
//         $('.game-field-inner').droppable({
//             scope: 'drop',
//             tolerance: 'touch',
//         });

//         $('.ship').draggable({
//             scope: 'drop',
//             containment: "body", 
//             revert: "invalid",
//             cursor: 'move',
//             opacity: 0.8,
//             snap: '.game-field-cell',
//             snapMode: "inner",
//         });
//     }

//     //Marking a cell with a ship's part as busy
//     function _dropShip(event, ui) {
//         $(this).removeClass('empty');
//         $(this).addClass('busy');
//         _getCoords($(this));
//     }

//     //Marking a cell without a ship's part as empty
//     function _dropOutShip(event, ui) {
//         $(this).removeClass('busy');
//         $(this).addClass('empty');
//     }

//     //Getting a set of ship's coords 
//     function _getCoords(elem) {
//         var _x = $(elem).closest('.game-field-cell').index();
//         var _y = $(elem).closest('.game-field-row').index();
//         console.log("{" + _x + ";" + _y + "}");
//     }

//     //Changing orientation of a ship on clicking
//     function _rotateShip() {
//         var shipW = $(this).width(),
//             shipH = $(this).height();

//         $(this).width(shipH);
//         $(this).height(shipW)  
//         _getCoords($(this));
//     }


//     return {
//         init: _setup,
//         getCoords : _getCoords
//     }
// })();


var gamebot = (function() {
    var _BOTMODE = "pvbserver",
        _PLAYERMODE = 'pvpserver',
        _IP = "46.32.76.190",
        _PORT = "8000",
        _msg = "",
        _socket = null,
        _lastShot = null,
        _loader = null;

    function _setupListeners() {
        $('#bot').on('click', function() {
            _closeSocket();
            _clearField();
            _setupSocket(_BOTMODE);
            $(this).addClass('inactive');
            $('#search').addClass('inactive');
            $('#random').removeClass('inactive');
            $('#confirm').removeClass('inactive');
        });
        $('#search').on('click', function() {
            _closeSocket();
            _clearField();
            _setupSocket(_PLAYERMODE);
            $(this).addClass('inactive');
            $('#bot').addClass('inactive');
            $('#random').removeClass('inactive');
            $('#confirm').removeClass('inactive');
        })
        $(window).on('unload', _closeSocket);

        $('#enemy .game-field-cell').on('click', _shotClickHandler)
        $('#random').on('click', _random);
        $('#confirm').on('click', _confirm);
    }

    function _clearField() {
        $('.ship-in').removeClass('ship-in');
        $('.game-field-cell').removeClass('busy empty hit <miss></miss>');
    }

    function _shotClickHandler() {
        var yVal = $(this).index(),
            xVal = $(this).closest('.game-field-row').index();
        _lastShot = { x: xVal, y: yVal };
        _send("Shot");
    }

    function _random() {
        var msg = '"PlaceShipsRandom"';
        _send(msg);
    }

    function _placeShips(data) {
        var rows = $($('.game-field-table')[0]).find($('tr')),
            field = data;

        for (var i = 0; i < 10; i++) {
            for (var j = 0; j < 10; j++) {
                var cell = rows.eq(i).children().eq(j).children('.game-field-inner');
                if (field[i][j] == 'Void') {
                    cell.removeClass('busy');
                    cell.closest('.game-field-cell').removeClass('ship-in');
                    cell.addClass('empty');
                } else if (field[i][j] == 'Ship') {
                    cell.removeClass('empty');
                    cell.addClass('busy');
                    cell.closest('.game-field-cell').addClass('ship-in');
                }
            }
        }
    }

    function _confirm() {
        var msg = '"StartBattle"';
        _send(msg);
        _setLoader();
    }

    function _setLoader() {
        _loader = $('#enemy').maskLoader({
            'background': 'black',
            'opacity': '0.3',
            'z-index': 999,
            'imgLoader': "http://www.animatedimages.org/data/media/271/animated-ship-image-0060.gif"
        });
    }

    function _shot(cell) {
        _send(JSON.stringify(_lastShot));
        _setLoader();
    }

    function _battleResultHandler(result) {
        if (result === "Win")
            alert("You won!");
        else if (result === "Lose")
            alert("You lost");
        else
            alert("WTF? We got neither WIN nor LOSE!");
    }

    function _noticeHandler(notice) {
        switch (notice) {
            case "YourTurn":
            case "OpponentFound":
            case "Error":
                _loader.destroy();
                break;
            case "ExpectedCoordinates":
                _shot();
                break;
            default:
                break;
        }
    }

    function _fieldChangesHandler(data) {
        var field = null;
        if (data.fieldStatus === "First") {
            field = $('#player');
        } else if (data.fieldStatus === "Second") {
            field = $('#enemy');
        } else {
            alert("what field did you mean?");
        }
        if (data.misses) {
            for (var i = 0; i < data.misses.length; i++) {
                var x = data.misses[i].x;
                var y = data.misses[i].y;
                _setFieldStatus(x, y, "miss");
            }
        }
        if (data.hit) {
            var x = data.hit.x;
            var y = data.hit.y;
            _setFieldStatus(x, y, "hit")
        }

        function _setFieldStatus(x, y, colorClass) {
            var row = field.find(".game-field-row").eq(x);
            row.find('.game-field-cell').eq(y).addClass(colorClass);
        }
    }

    function _setupSocket(mode) {
        _socket = new WebSocket("ws://" + _IP + ":" + _PORT + "/" + mode);

        _socket.onopen = function() {
            console.log("Connected successfuly");
            if (mode === _PLAYERMODE) {
                _loader = $('body').maskLoader({
                    'background': 'white',
                    'opacity': '0.7',
                    'z-index': 999,
                    'imgLoader': "http://www.animatedimages.org/data/media/271/animated-ship-image-0046.gif"
                });
            }
        }
        _socket.onerror = function(error) {
            console.log("ERROR: " + error.data);
        }
        _socket.onmessage = function(event) {
            msg = JSON.parse(event.data);
            console.log("Got a message with type: " + msg.type + "; data: " + msg.data);
            switch (msg.type) {
                case "Cell[][]":
                    _placeShips(msg.data);
                    break;
                case "Notice":
                    _noticeHandler(msg.data);
                    break;
                case "BattleResult":
                    _battleResultHandler(msg.data);
                    break;
                case "FieldChanges":
                    _fieldChangesHandler(msg.data);
                    break;
                default:
                    console.log("Not in switch");
            }
        }
        _socket.onclose = function(event) {
            if (event.wasClean)
                console.log("Connection closed clearly");
            else
                console.log("Connection was broken");
            console.log("Code: " + event.code + " reason: " + event.reason);
        }
    }

    function _send(msg) {
        if (!_socket) {
            console.log("Socket was not set (" + _socket + ")");
            return;
        }
        console.log("Sending: " + msg);
        _socket.send(msg);
    }

    function _closeSocket() {
        if (_socket)
            _socket.close();
    }

    return {
        init: _setupListeners
    }
})();




//Main part
$(document).ready(function() {
    if ($.find("#fullpage").length > 0)
        scroll.init();

    // if($.find('.ship').length > 0) {
    // dragAndDrop.init();
    // }
    if ($.find('.game-section').length > 0)
        gamebot.init();
});
