YAHOO.namespace('ForexInvest.SystemMonitor');
(function () {
    //define a few shortcuts
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Connect = YAHOO.util.Connect;

    YAHOO.ForexInvest.SystemMonitor = function (container) {
        this.container = container;
    };
    YAHOO.ForexInvest.SystemMonitor.prototype = {
        init : function () {

            // Instantiate a Panel from markup
            //todo cmeng create my panel wiget to disply computer monitoring info and jsm server connection info and etc.
            this.computerPanel = new YAHOO.widget.Panel('computerPanel', { width : "320px", draggable:false, close:false });
            this.computerPanel.setHeader("Panel #2 from Script");
            this.computerPanel.setBody("This is a dynamically generated Panel.");
            this.computerPanel.setFooter("End of Panel #2");


            this.jsmServerPanel = new YAHOO.widget.Panel('jsmServerPanel', { width : "320px",draggable:false, close:false });
            this.jsmServerPanel.setHeader("Panel jsmServerPanel");
            this.jsmServerPanel.setBody("This is a dynamically generated Panel.");
            this.jsmServerPanel.setFooter("End of Panel #2");

            var that = this;
            Connect.asyncRequest('get', '/app/tab/console', {
                success: that.initPanels,
                failure: null,
                argument: { that: that }
            }, null);

            //this.jsmServerPanel.render(this.container);

            /*// Instantiate a Panel from script
            YAHOO.example.container.panel2 = new YAHOO.widget.Panel("panel2", { width : "320px", visible : false, draggable : false, close : false });
            YAHOO.example.container.panel2.setHeader("Panel #2 from Script");
            YAHOO.example.container.panel2.setBody("This is a dynamically generated Panel.");
            YAHOO.example.container.panel2.setFooter("End of Panel #2");
            YAHOO.example.container.panel2.render("container");*/
        },

        initPanels : function(response){
            var that = response.argument.that;
            that.container.innerHTML = response.responseText;

            var computerPanelContainer = Selector.query(".computer", this.container, true);
            that.computerPanel.render(computerPanelContainer);

            var jsmServerPanelContainer = Selector.query(".jms-server", this.container, true);
            that.jsmServerPanel.render(jsmServerPanelContainer);
        }
    }

})();

