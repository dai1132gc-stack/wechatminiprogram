const request = require('../utils/request')

/** GET /houses：分页 + 筛选 */
function listHouses(query = {}) {
  return request.get('/houses', query)
}

/** GET /houses/{id} */
function getHouse(id) {
  return request.get(`/houses/${id}`)
}

module.exports = { listHouses, getHouse }
