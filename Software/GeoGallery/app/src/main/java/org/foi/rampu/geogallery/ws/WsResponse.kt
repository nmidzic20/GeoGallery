package org.foi.rampu.geogallery.ws

data class Query(
    var pages : Map<Int,Id>?
)

data class Pages(
    var id : Id?
)

data class Id(
    var pageid : Int?,
    var ns : Int?,
    var title : String?,
    var extract : String?
)
data class WsResponse (
    var batchcomplete : String?,
    var query : Query?,
)