# Exercice 2 : Les services Jakarta

## Premier service

Cet exercice va introduire la gestion des services via Jakarta, une alternative à l’IOC de Spring.

Créons notre premier service :

```java
@ApplicationScoped
public class GreetingService {
    public String hello(){
        return "RESTEasy Reactive";
    }
}
```



On constate que l’annotation pour ajouter un bean au contexte applicatif se fait via l’annotation @ApplicationScoped. Cette annotation, en plus de mettre une instance du service au contexte, définit le scope du bean.

Il existe plusieurs scopes

| Scope             | Description                                                                                   |
|:------------------|:----------------------------------------------------------------------------------------------|
| ApplicationScoped | Le bean est instancié au lancement de l’application                                           |
| Singleton         | Le bean est instancié au lancement de l’application                                           |
| RequestScoped     | Le bean n’est instancié que pour une requête                                                  |
| Dependent         | Le bean sera instancié lors de l’instanciation des beans dans lesquels ils sont injectés      |
| SessionScoped     | Le bean n’est valable que pour une session. Ne fonctionne qu’avec le module quarkus-undertow  |

@Singleton et @ApplicationScoped sont similaires d’un point de vue instanciation mais totalement différents d’un point de vue technique.
@Singleton est identique à un design pattern singleton Java classique, ce qui rend la rédaction des tests compliqués car le bean ne peut pas être mocké.
@ApplicationScoped passe par un système de proxy, ce qui permet de mocker les Bean pour les tests sans impacter l’application.



Modifions le contrôleur :
```java
@Path("/hello")
public class GreetingResource {
@Inject
private GreetingService greetingService;

    @GET 
    @Produces(MediaType.TEXT_PLAIN) 
    public String hello() { 
        return greetingService.hello(); 
    } 
}
```

Pour récupérer un bean du contexte, il suffit d’annoter la propriété avec @Inject.

/!\ Il faut bien prend l’import de Jakarta.



Ouvrons le endpoint http://localhost:8080/hello

Le message affiché est bien celui du service. Le code a été pris à chaud.

Dans la console exécutant le serveur, tapez “r”. Les tests sont lancés et plantent.



Corrigez le message retourné par le service pour faire passer les tests.

## Override de service

Maintenant essayons de créer une déclinaison de notre service.
```java
@ApplicationScoped
public class CustomGreetingService extends GreetingService {
    @Override
    public String hello() {
        return "override service!!!";
    }
}
```



Rechargeons le lien http://localhost:8080/hello.

Surprise ! Cela ne marche plus.

Pourquoi ? Les bean sous Jakarta sont régis par deux éléments :
- le scope,
- le qualifier.

Le scope a déjà été abordé précédemment. Concentrons-nous sur le qualifier.

Un qualifier est une annotation permettant de distinguer les beans. Si aucun qualifier n’est présent alors le bean prend le qualifier default.

Créons un qualifier pour permettre à l’application de fonctionner.
```java
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
public @interface CustomQualifier {
}
```



Modifions notre service pour l’associer à ce nouveau qualifier.
```java
@ApplicationScoped
@CustomQualifier
public class CustomGreetingService extends GreetingService {
...
```

Dans l’état, si l’on recharge notre page de test, cela refonctionne mais en utilisant le service d’origine, pas son extension.

Modifions notre controller pour exploiter notre nouveau service.
```java
@Inject
@CustomQualifier
private GreetingService greetingService;
```
Avec cette modification, un rechargement de la page de test affiche bien le nouveau message.

L'injection est donc régie par le qualifier. La classe demandée et le qualifier permettent donc de switcher d’une version à l’autre. Voici le mapping du bean injecté en fonction de la classe demandée et du qualifier.

| Class demandée   | Qualifier       | Bean injecté          |
|:-----------------|:----------------|:----------------------|
| GreetingService  | default         | GreetingService       |
| GreetingService     | CustomQualifier | CustomGreetingService |
| CustomGreetingService | default         | ERREUR                |
| CustomGreetingService | CustomQualifier | CustomGreetingService |




On constate que la demande du qualifier est stricte, il n’y aura pas d’injection si aucun bean n’est défini pour se qualifier, même si une seule version du bean existe mais avec un qualifier différent.

La gestion des beans sous Jakarta est plus riche que cela (définition de properties en tant que bean, résultat d’une méthode en tant que bean, ajout de decorator...). Je vous laisse approfondir Jakarta sur ces divers aspects si cela vous tente.

[ Exercice suivant ](https://github.com/trzebiatowskif/initiation-quarkus/blob/main/exercice-3/README.md)