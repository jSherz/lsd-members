import { plugin } from './BlippPlugin';
import * as Blipp from 'blipp';

describe('plugins/BlippPlugin', () => {

    it('registers the correct plugin', () => {
        expect(plugin.register).toEqual(Blipp);
    });

});
