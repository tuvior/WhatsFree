import webapp2
import string
import random
from google.appengine.ext import ndb

def response( status = 0, cookie='' ):
    if status == 0:
        res = """{
    "login": {
        "status": "ok",
        "cookie": \"""" + cookie + """\"
    }
}
        """
    elif status == 1:
        res = """{
    "login": {
        "status": "failure",
        "reason": "user"
    }
}
        """
    elif status == 2:
        res = """{
    "login": {
        "status": "failure",
        "reason": "password"
    }
}
        """
    elif status == -1:
        res = "invalid request"
    return res


class User(ndb.Model):
    username = ndb.StringProperty()
    pswd = ndb.StringProperty()
    email = ndb.StringProperty()

class Login(webapp2.RequestHandler):
            
    def get(self):
        username = self.request.get("user")
        password = self.request.get("password")
        
        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'
        
        if not username or not password:
            self.response.write(response(-1))
        else:
            cookie = ''.join(random.choice(string.ascii_uppercase + string.ascii_lowercase + string.digits) for _ in range(64)) 
            query_user = User.query(User.username == username).fetch()
            if query_user:
                query = User.query(ndb.AND(User.username == username, User.pswd == password)).fetch()
                if query:
                    self.response.write(response(0, cookie))   
                else: 
                    self.response.write(response(2))   
            else:
                self.response.write(response(1))


app = webapp2.WSGIApplication([
    ('/login', Login),
], debug=True)