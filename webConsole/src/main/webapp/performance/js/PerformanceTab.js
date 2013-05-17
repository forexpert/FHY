YAHOO.namespace('ForexInvest.PerformanceTab');
(function () {
    //define a few shortcuts
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Connect = YAHOO.util.Connect;
    var jsonHelper = YAHOO.lang.JSON;

    YAHOO.ForexInvest.PerformanceTab = function (container) {
        this.container = container;
    };
    YAHOO.ForexInvest.PerformanceTab.prototype = {
        init : function () {
            this.container.innerHTML =
                "<div class='performance-container'>" +
                    "<div class='performance-data'><div class='baseInfo'></div><div class='equityChart'></div><div class='closePositions'></div></div>" +
                    "<div class='control'>" +
                        "<div class='buttons'>" +
                            "<p><button class='startTest'>Start Back Testing</button></p>" +
                            "<p><input type='text' class='from' value='2011.03.21 00:00:00 +0000'>From</input>" +
                            "<input type='text' class='to' value='2012.03.21 00:00:10 +0000'>To</input></p>" +
                            "<p> Status<div class='status'>Not started!</div></p>" +


                        "</div>" +
                        "<div class='clientPanel'>" +

                        "</div>" +

                        "<div class='strategyPanel'>" +

                        "</div>" +
                    "</div>" +

                "</div>";

            this.clientPanel = Selector.query(".performance-container .clientPanel", this.container, true);
            this.strategyPanel = Selector.query(".performance-container .strategyPanel", this.container, true);
            this.buttonPanel = Selector.query(".performance-container .control .buttons", this.container, true);
            this.startTestButtonEL = Selector.query(".startTest", this.buttonPanel, true);
            Event.addListener(this.startTestButtonEL, "click", this.startTest, this, true);
            this.testFromEL = Selector.query(".from", this.buttonPanel, true);
            this.testToEL = Selector.query(".to", this.buttonPanel, true);
            this.status = Selector.query(".status", this.buttonPanel, true);
            this.initClientAndStrategyPanel();
            this.refreshBackTestingStatus();
        },

        updateStatusText: function(text){
            this.status.innerHTML = text;
        },

        startTest: function(event) {
            Event.preventDefault(event);
            var that = this;
            Connect.asyncRequest('post', '/app/system-console/startBackTesting', {
                success : function (response) {
                    var json = jsonHelper.parse(response.responseText);
                    if(json.StartBackTesting){
                        response.argument.that.updateStatusText("send request to start back testing!")
                    }
                },
                failure : null,
                argument : { that : that }
            }, "from="+encodeURIComponent(this.testFromEL.value)+"&&to="+encodeURIComponent(this.testToEL.value));
        },

        refreshBackTestingStatus: function(){
            var that = this;
            Connect.asyncRequest('get', '/app/system-console/getBackTestingStatus', {
                success : function (response) {
                    var json = jsonHelper.parse(response.responseText);
                    response.argument.that.updateStatusText(json.status?"In Testing...": "Test Not Started");
                    setTimeout(function(){that.refreshBackTestingStatus()}, 3000);
                },
                failure : null,
                argument : { that : that }
            }, null);
        },

        getTestClientPerformanceData: function(clientId){
            var drawEquityChart = function(records, chartContainer){
                google.load("visualization", "1", {packages:["corechart"]});
                /*google.setOnLoadCallback(drawChart);
                function drawChart() {*/
                    var data = google.visualization.arrayToDataTable([
                        ['Year', 'Sales', 'Expenses'],
                        ['2004',  1000,      400],
                        ['2005',  1170,      460],
                        ['2006',  660,       1120],
                        ['2007',  1030,      540]
                    ]);

                    var options = {
                        title: 'Company Performance'
                    };

                    var chart = new google.visualization.LineChart(chartContainer);
                    chart.draw(data, options);
                /*}*/

            };
            var that = this;
            Connect.asyncRequest('get', '/app/system-console/getTestBrokerClientPerformanceData?testBrokerClientId='+clientId, {
                success : function (response) {
                    var json = jsonHelper.parse(response.responseText);
                    var baseInfoEL = Selector.query(".performance-container .performance-data .baseInfo", this.container, true);
                    var equityChartEL = Selector.query(".performance-container .performance-data .equityChart", this.container, true);
                    var closePositionsEL = Selector.query(".performance-container .performance-data .closePositions", this.container, true);

                    baseInfoEL.innerHTML = "<p><H1>Client "+ json.testClientName +" Performance Report</H1></p>" +
                        "<p>Test Strategy is " + json.strategyname + " </p>" +
                        "<p>Start Equity: " + json.equityRecords[0].equity + " </p>" +
                        "<p>End Equity: " + json.equityRecords[json.equityRecords.length-1].equity + " </p>";

                    drawEquityChart(json.equityRecords, equityChartEL);
                    //that.initClosePositionTable(json.closePositions);
                    //that.initEquityChart(json.equityRecords);

                },
                failure : null,
                argument : { that : that }
            }, null);
        },

        initClientPanel : function (clients) {
            var myColumnDefs = [
                {key:"ClientId", sortable:true, resizeable:true},
                {key:"StrategyName", sortable:true, resizeable:true}
            ];

            var myDataSource = new YAHOO.util.DataSource(clients);
            myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
            myDataSource.responseSchema = {
                fields: ["ClientId","StrategyName"]
            };

            this.clientTable = new YAHOO.widget.DataTable(this.clientPanel,
                myColumnDefs, myDataSource, {caption:"Test Broker Client List"});
            var that = this;
            var handleClientTableCellClick = function(oArgs){
                var e = oArgs.event;
                var cell = oArgs.target;
                var target = Event.getTarget(e);
                Event.preventDefault(e);

                switch(that.clientTable.getColumn(cell).key){
                    case "ClientId":
                        that.getTestClientPerformanceData(that.clientTable.getRecord(target).getData("ClientId"));
                        break;
                }
            };
            this.clientTable.subscribe('cellClickEvent', handleClientTableCellClick);

        },




        initStrategyPanel : function (strategies) {
            var myColumnDefs = [
                {key:"StrategyName", sortable:true, resizeable:true}
            ];

            var myDataSource = new YAHOO.util.DataSource(strategies);
            myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
            myDataSource.responseSchema = {
                fields: ["StrategyName"]
            };

            new YAHOO.widget.DataTable(this.strategyPanel,
                myColumnDefs, myDataSource, {caption:"StrategyName List"});
        },


        initClientAndStrategyPanel : function () {
            var that = this;
            Connect.asyncRequest('get', '/app/system-console/getBackTestingInfo', {
                success : function (response) {
                    var json = jsonHelper.parse(response.responseText);
                    var clients = json.clients;
                    var strategies = json.strategies;
                    that.initClientPanel(clients);
                    that.initStrategyPanel(strategies);

                },
                failure : null,
                argument : { that : that }
            }, null);
        }
    }
})();