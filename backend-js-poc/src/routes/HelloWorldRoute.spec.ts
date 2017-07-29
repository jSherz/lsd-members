import { route } from './HelloWorldRoute';

describe('routes/HelloWorldRoute', () => {

    it('has the correct method and path', () => {
        expect(route.method).toEqual('GET');
        expect(route.path).toEqual('/');
    });

    it('always says hello world', () => {
        const reply: any = jasmine.createSpy('reply');
        route.handler(null, reply);

        expect(reply).toHaveBeenCalledWith('Hello, world!');
    });

});
