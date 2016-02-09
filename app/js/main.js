var scroll = (function() {
    var _fullpageElem = $('#fullpage'),

        _addScroll = function() {
            _fullpageElem.fullpage({
                anchors:['main','about'],
                menu: '#nav'
            });
        };

    return {
        init: _addScroll
    }
})();

var easterEgg = (function() {
    var _btn = $('.start-game-btn')[0],
        _counter = 0;

    function _setUpListeners() {
        $(_btn).on('click', _getIt);
    }

    _getIt = function(ev) {
        ev.preventDefault();
        _counter++;
        if (_counter > 4) {
            $('.header-title').text("Batalŝipo");
            $($('.nav-link')[0]).text("Estra paĝo");
            $($('.nav-link')[1]).text("Teamo");
            $('.intro-title').text("Batalŝipo");
            $('.intro-content').text("Tie estas la reta versio de la populara tabulludo, kiun eble ludis ĉiuj! Meti ŝipoj sur la batalkampo! Fajro! La plej forta homo Gajnos!");
            $(this).text("Komenci ludon");
        }

    };

    return {
        init: _setUpListeners
    }
})();

$(document).ready(function() {
    if ($.find("#fullpage").length > 0) {
        scroll.init();
    }

    easterEgg.init();
});