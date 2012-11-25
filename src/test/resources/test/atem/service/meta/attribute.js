{
	"ext_type" : "schema:schema",
	"code" : "schema:attribute",
	"label" : "schema:attribute",
	"attributes" : [ {
		"ext_type" : "schema:list-attribute",
		"required" : false,
		"type" : {
			"ext_type" : "schema:type-ref",
			"code" : "schema:type-ref"
		},
		"label" : "validTypes",
		"code" : "validTypes",
		"array" : true
	}, {
		"ext_type" : "schema:single-attribute",
		"required" : false,
		"type" : {
			"ext_type" : "schema:type-ref",
			"code" : "boolean"
		},
		"label" : "required",
		"code" : "required",
		"array" : false
	}, {
		"ext_type" : "schema:single-attribute",
		"required" : false,
		"type" : {
			"ext_type" : "schema:type-ref",
			"code" : "schema:type-ref"
		},
		"label" : "type",
		"code" : "type",
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
		"ext_type" : "schema:list-attribute",
		"required" : false,
		"type" : {
			"ext_type" : "schema:type-ref",
			"code" : "text"
		},
		"label" : "values",
		"code" : "values",
		"array" : true
	}, {
		"ext_type" : "schema:single-attribute",
		"required" : false,
		"type" : {
			"ext_type" : "schema:type-ref",
			"code" : "text"
		},
		"label" : "dateformat",
		"code" : "dateformat",
		"array" : false
	} ],
	"type_property" : "ext_type"
}
