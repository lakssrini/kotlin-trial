package demo

import java.io.File
import khttp.get


data class Rent(val zipCode: String, val county: String, val city: String, val state: String, val rent: Double)
data class Property(val address: String, val city: String, val zipCode: String, val state: String, val price: Double, val rent: Double)

fun main(args: Array<String>) {
    var rentMap: MutableMap<String, Rent>  = mutableMapOf()
    var propertyMap: MutableList<Property> = mutableListOf()
    val urls = listOf(
        "https://www.opendoor.com/api/v1/listings/ordered_listings?active=true&market_name=dallas",
        "https://www.opendoor.com/api/v1/listings/ordered_listings?active=true&market_name=phoenix",
        "https://www.opendoor.com/api/v1/listings/ordered_listings?active=true&market_name=las_vegas",
        "https://www.opendoor.com/api/v1/listings/ordered_listings?active=true&market_name=atlanta",
        "https://www.opendoor.com/api/v1/listings/ordered_listings?active=true&market_name=orlando",
        "https://www.opendoor.com/api/v1/listings/ordered_listings?active=true&market_name=raleigh",
        "https://www.opendoor.com/api/v1/listings/ordered_listings?active=true&market_name=san_antonio",
        "https://www.opendoor.com/api/v1/listings/ordered_listings?active=true&market_name=charlotte",
        "https://www.opendoor.com/api/v1/listings/ordered_listings?active=true&market_name=nashville",
        "https://www.opendoor.com/api/v1/listings/ordered_listings?active=true&market_name=tampa"
    )

    try {
        var lines = File("/Users/laks/git/home/rental.csv").useLines(Charsets.UTF_8) {
            it.toList()
        }
        lines.forEachIndexed { index, s ->
            if (index > 0) {
                val properties = s.split(",")
                var zipCode = properties[1].replace("\"", "")
                val rent = Rent(zipCode, properties[4], properties[5], properties[2], properties[7].toDouble())
                rentMap.put(zipCode, rent)
            }

        }
    }
    catch (e: Exception) {
        e.printStackTrace()
    }


    for (url in urls) {
        val r = get(
            url,
            headers = mapOf(
                "user-agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36"
            )
        )
        val results = r.jsonArray
        for (i in 0..(results.length() - 1)) {
            val item = results.getJSONObject(i)
            val zipCode: String = item.getJSONObject("address").getString("postal_code")
            val rentObject: Rent? = rentMap.get(zipCode)
            var rent: Double = 0.0
            if (rentObject != null) {
                rent = rentObject.rent
            }

            val property = Property(
                item.getJSONObject("address").getString("street"),
                item.getJSONObject("address").getString("city"),
                zipCode,
                item.getJSONObject("address").getString("state"),
                item.getDouble("price_cents") / 100,
                rent
            )
            propertyMap.add(property)
        }
    }

    println(propertyMap)
}
