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

$(document).ready(function() {
    if ($.find("#fullpage").length > 0) {
        scroll.init();
    }
});