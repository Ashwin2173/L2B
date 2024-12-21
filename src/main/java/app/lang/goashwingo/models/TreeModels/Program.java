package app.lang.goashwingo.models.TreeModels;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Program {
    String version;
    Set<String> imports;
    List<Statement> body;
}
