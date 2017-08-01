import * as Blipp from 'blipp';
import { plugin } from './BlippPlugin';

describe('plugins/BlippPlugin', () => {

    it('registers the correct plugin', () => {
        expect(plugin.register).toEqual(Blipp);
    });

});
