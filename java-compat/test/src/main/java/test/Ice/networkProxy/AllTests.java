// **********************************************************************
//
// Copyright (c) 2003-2017 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package test.Ice.networkProxy;

import java.io.PrintWriter;

import test.Ice.networkProxy.Test.TestIntfPrx;
import test.Ice.networkProxy.Test.TestIntfPrxHelper;

public class AllTests
{
    private static void
    test(boolean b)
    {
        if(!b)
        {
            throw new RuntimeException();
        }
    }

    public static void
    allTests(test.Util.Application app)
    {
        Ice.Communicator communicator = app.communicator();
        PrintWriter out = app.getWriter();

        String sref = "test:" + app.getTestEndpoint(0);
        Ice.ObjectPrx obj = communicator.stringToProxy(sref);
        test(obj != null);

        int proxyPort = communicator.getProperties().getPropertyAsInt("Ice.HTTPProxyPort");
        if(proxyPort == 0)
        {
            proxyPort = communicator.getProperties().getPropertyAsInt("Ice.SOCKSProxyPort");
        }

        TestIntfPrx test = TestIntfPrxHelper.checkedCast(obj);
        test(test != null);

        out.print("testing connection... ");
        out.flush();
        {
            test.ice_ping();
        }
        out.println("ok");

        out.print("testing connection information... ");
        out.flush();
        {
            Ice.IPConnectionInfo info = null;
            for(Ice.ConnectionInfo p = test.ice_getConnection().getInfo(); p != null; p = p.underlying)
            {
                if(p instanceof Ice.IPConnectionInfo)
                {
                    info = (Ice.IPConnectionInfo)p;
                }
            }
            test(info.remotePort == proxyPort); // make sure we are connected to the proxy port.
        }
        out.println("ok");

        out.print("shutting down server... ");
        out.flush();
        {
            test.shutdown();
        }
        out.println("ok");

        out.print("testing connection failure... ");
        out.flush();
        {
            try
            {
                test.ice_ping();
                test(false);
            }
            catch(Ice.LocalException ex)
            {
            }
        }
        out.println("ok");
    }
}
