const { envs, currentEnv } = require('./env')

const config = envs[currentEnv()]

module.exports = config
