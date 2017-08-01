import * as bunyan from 'bunyan';
import { register } from 'good';
import { plugin } from './GoodPlugin';

describe('plugins/GoodPlugin', () => {

    it('has the correct options', () => {
        const pluginWithLevel = plugin('debug');

        expect(pluginWithLevel.options.reporters.bunyan[0].args[1].logger.level()).toEqual(bunyan.DEBUG);
        expect(pluginWithLevel.options.reporters.bunyan[0].module).toEqual('good-bunyan');

        expect(pluginWithLevel.register).toEqual(register);
    });

    it('accepts different log levels', () => {
        const pluginWithInfo = plugin('info');
        const pluginWithError = plugin('error');

        expect(pluginWithInfo.options.reporters.bunyan[0].args[1].logger.level()).toEqual(bunyan.INFO);
        expect(pluginWithError.options.reporters.bunyan[0].args[1].logger.level()).toEqual(bunyan.ERROR);
    });

});
