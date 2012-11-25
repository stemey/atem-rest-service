{
	"ext_type" : "schema:schema",
	"code" : "editor:schema",
	"label" : "editor:schema",
	"attributes" : [ {
		"ext_type" : "schema:single-attribute",
		"required" : false,
		"type" : {
			"ext_type" : "schema:type-ref",
			"code" : "text"
		},
		"code" : "type-property",
		"array" : false
	}, {
		"ext_type" : "schema:list-attribute",
		"required" : false,
		"type" : {
			"ext_type" : "schema:type-ref",
			"code" : "editor:attribute"
		},
		"label" : "attributes",
		"code" : "attributes",
		"array" : true
	}, {
		"ext_type" : "schema:single-attribute",
		"required" : false,
		"type" : {
			"ext_type" : "schema:type-ref",
			"code" : "text"
		},
		"label" : "code",
		"code" : "code",
		"array" : false
	}, {
		"ext_type" : "schema:single-attribute",
		"required" : false,
		"type" : {
			"ext_type" : "schema:type-ref",
			"code" : "text"
		},
		"label" : "label",
		"code" : "label",
		"array" : false
	} ],
	"type_property" : "ext_type"
}