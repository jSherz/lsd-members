import { register } from 'good';
import { plugin } from './GoodPlugin';

describe('plugins/GoodPlugin', () => {

    it('has the correct options', () => {
        expect(plugin.register).toEqual(register);

        expect(plugin.options.reporters.console[0].module).toEqual('good-squeeze');
        expect(plugin.options.reporters.console[1].module).toEqual('good-console');
    });

});
