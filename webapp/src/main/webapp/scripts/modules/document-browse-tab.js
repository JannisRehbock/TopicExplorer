define(["knockout", "jquery"],
function(ko, $) {
	var instance;
    function Singleton (data) {
		// this could be loaded from somewhere else, however all AMD modules need to be loaded in advance
		var extend = new Array("scripts/modules/extenders/document-browse-time","scripts/modules/extenders/document-browse-text");
		// here comes the show-document-tab 
		var self = {};
		
		self.TextRepresentation = function(label, field) {
    		this.label = label;
    		this.field = field;
    	};
		
		self.documentLimit = 20;
		
		self.browseData= new Object();
		
		self.loading = ko.observable(false);
		
		self.firstLoading = ko.observable(false);
		
		self.selectedTopics = ko.observableArray(globalData.TOPIC_SORTING).subscribeTo("selectedTopics");

		self.selectedTopics.subscribe(function(newValue) {
			setTopTopics(self.browseData[self.active()].selectedDocuments(), false);
		});
		
		
		self.scrollCallback = function(el) {
			$("#desktop").children(".documentList").children(".jumpToStart").css('top',($("#desktop").scrollTop() - 10) + 'px');
			self.loadMoreDocuments();
			
			if($("#desktop").scrollTop() > 1000) {
				$("#desktop").children(".documentList").children(".jumpToStart").show();
			} else {
				$("#desktop").children(".documentList").children(".jumpToStart").hide();
			}
		};
		
		self.changeSorting = function(newValue) {
			if(!self.loading()) {
				self.browseData[self.active()].documentsFull(false);
				self.loading(true);
				self.browseData[self.active()].selectedDocuments([]);
				$.getJSON("JsonServlet?Command=" + self.active() + "&sorting=" + newValue)
				.success(function(receivedParsedJson) {
					self.browseData[self.active()].nextOffset = self.documentLimit;
					if(receivedParsedJson.DOCUMENT_SORTING.length < self.documentLimit) {
						self.browseData[self.active()].documentsFull(true);
					} else {
						self.browseData[self.active()].documentsFull(false);
					}
					$.extend(globalData.DOCUMENT, receivedParsedJson.DOCUMENT);
					
					setTopTopics(receivedParsedJson.DOCUMENT_SORTING, true);
					
					self.browseData[self.active()].selectedDocuments(receivedParsedJson.DOCUMENT_SORTING);
					for (var i=0;i<extend.length;i++) {
			 			var extender = require(extend[i]);
						extender(self);
					}
					self.loading(false);
				});			
			}
		};
		
		self.checkSize = function() {
			if($(".browser").length > 0 ) {
				self.loadMoreDocuments();
			}
		};
		
		self.loadMoreDocuments = function() {
			if(!self.loading() && !self.browseData[self.active()].documentsFull() && $("#desktop").scrollTop() + $("#desktop").height() + 90 >= $("#desktop")[0].scrollHeight) {
				self.loading(true);
				$.getJSON("JsonServlet?Command=" + self.active() + "&offset=" + self.browseData[self.active()].nextOffset + "&sorting=" + self.browseData[self.active()].selectedSorting())
				.success(function(receivedParsedJson) {
					self.browseData[self.active()].nextOffset += self.documentLimit;
					if(receivedParsedJson.DOCUMENT_SORTING.length < self.documentLimit) {
						self.browseData[self.active()].documentsFull(true);
					}
					$.extend(globalData.DOCUMENT, receivedParsedJson.DOCUMENT);
					
					setTopTopics(receivedParsedJson.DOCUMENT_SORTING, true);
					
					self.browseData[self.active()].selectedDocuments(self.browseData[self.active()].selectedDocuments().concat(receivedParsedJson.DOCUMENT_SORTING));
					for (var i=0;i<extend.length;i++) {
			 			var extender = require(extend[i]);
						extender(self);
					}
					self.loading(false);
				});
				
			}
		};
		
		self.loadDocument = function(docId) {
			var postData = $.extend({}, self.browseData[self.active()].data);
			delete(postData.getParam);
			postData.documentId = docId;
			ko.postbox.publish('openNewTab', {moduleName:"document-show-tab", tabHeading:"Show Document " + docId, data:postData});
		};
		
		self.moveToTopic = function(topic) {
			ko.postbox.publish('moveToTopic', topic);
		};
		
		globalData.DOCUMENT ={};
		
		self.setData = function (data) { 
			self.active = ko.observable(data.getParam);
			if (!self.browseData[self.active()]) {
				self.loading(true);
				self.firstLoading(true);
				self.browseData[self.active()] = {};
				self.browseData[self.active()].data = data;
				self.browseData[self.active()].sortingOptions = ko.observableArray(['RELEVANCE']);
				self.browseData[self.active()].selectedSorting = ko.observable('RELEVANCE');
				self.browseData[self.active()].selectedSorting.subscribe(self.changeSorting);
				self.browseData[self.active()].selectedDocuments = ko.observableArray([]);
				self.browseData[self.active()].textSelectArray = ko.observableArray([]);
				self.browseData[self.active()].textSelection = ko.observable();
				$.getJSON("JsonServlet?Command=" + self.active() + "&sorting=" + self.browseData[self.active()].selectedSorting())
				.success(function(receivedParsedJson) {
					self.browseData[self.active()].nextOffset = self.documentLimit;
					if(receivedParsedJson.DOCUMENT_SORTING.length < self.documentLimit) {
						self.browseData[self.active()].documentsFull = ko.observable(true);
					} else {
						self.browseData[self.active()].documentsFull = ko.observable(false);
					}
					$.extend(globalData.DOCUMENT, receivedParsedJson.DOCUMENT);
					setTopTopics(receivedParsedJson.DOCUMENT_SORTING, true);
					self.browseData[self.active()].selectedDocuments(receivedParsedJson.DOCUMENT_SORTING);
					self.browseData[self.active()].textSelectArray.push(new self.TextRepresentation('Keywords', 'KEYWORD_'));
					self.browseData[self.active()].textSelection(new self.TextRepresentation('Keywords', 'KEYWORD_'));
					
					for (var i=0;i<extend.length;i++) {
			 			var extender = require(extend[i]);
						extender(self);
					}
					self.loading(false);
					self.firstLoading(false);
				});
			} 
		};
		self.getData = function () { return self.active(); };
		self.setData(data);
		
		self.windowWidth = ko.observable(Math.max(800, $(window).width(), /* For opera: */ document.documentElement.clientWidth)).subscribeTo("windowWidth");
		
		self.documentElementWidth = ko.computed (function() {
			var documentWidth = 262;
			var docDeskRatio = Math.floor((self.windowWidth() - 10) / documentWidth);
			return ((self.windowWidth() - 10) / docDeskRatio) - 32;
		});
		
		function setTopTopics(documents, isNew) {
			for(docIndex in documents) {	
				docId = documents[docIndex];
				bestTopics = [];
				count = 0;
				for(topicIndex in globalData.DOCUMENT[docId].TOP_TOPIC) {
					topicId = globalData.DOCUMENT[docId].TOP_TOPIC[topicIndex];
					if(self.selectedTopics.indexOf(topicId) > -1) {
						bestTopics.push(topicId);
						count++;
						if(count > 3) break;
					}
				}
				if(isNew) {
					globalData.DOCUMENT[docId].topTopics = ko.observableArray(bestTopics);
				} else {
					globalData.DOCUMENT[docId].topTopics(bestTopics);
				}
			}	
		} 
		
		return self;
	};

	return function getSingleton(data) {
		if (instance) { // already exists
			instance.setData(data); // pass the data from view
			return instance; // that already exists
			
		} else { // instance is not existing
			return (instance = new Singleton(data)); // create a new one with data from view
			
		};	
	};
});